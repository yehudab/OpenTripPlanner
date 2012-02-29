/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package org.opentripplanner.api.ws.transit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.opentripplanner.api.model.transit.RouteGeometrySet;
import org.opentripplanner.routing.graph.Edge;
import org.opentripplanner.routing.services.GraphService;
import org.opentripplanner.routing.services.TransitIndexService;
import org.opentripplanner.routing.transit_index.RouteVariant;
import org.opentripplanner.util.MapUtils;
import org.opentripplanner.util.PolylineEncoder;
import org.springframework.beans.factory.annotation.Required;

import com.sun.jersey.api.spring.Autowire;
import com.sun.jersey.spi.resource.Singleton;
import com.vividsolutions.jts.geom.Geometry;

@Path("/shapes")
@XmlRootElement
@Autowire
@Singleton
public class RouteData {

    private GraphService graphService;

    @Required
    public void setGraphService(GraphService graphService) {
        this.graphService = graphService;
    }

    @GET
    @Path("/routes")
    @Produces({ MediaType.APPLICATION_JSON })
    public RouteGeometrySet getEdgesByVariant() {
        RouteGeometrySet geometrySet = new RouteGeometrySet();
        TransitIndexService transitIndex = graphService.getGraph().getService(TransitIndexService.class);

        HashMap<HashableGeometry, Integer> edgeMap = new HashMap<HashableGeometry, Integer>();
        HashMap<HashableGeometry, List<RouteVariant>> edgeToVariantSet = new HashMap<HashableGeometry, List<RouteVariant>>();
        
        for (AgencyAndId route : transitIndex.getAllRouteIds()) {
            for (RouteVariant variant : transitIndex.getVariantsForRoute(route)) {
                
                for (Edge e : variant.getClosestStreetEdges()) {
                    HashableGeometry g = new HashableGeometry(e.getGeometry());
                    MapUtils.addToMapListUnique(edgeToVariantSet, g, variant);
                }
            }
        }
        
        HashMap<List<RouteVariant>, List<Geometry>> allTrunkGeometry = new HashMap<List<RouteVariant>, List<Geometry>>();
        HashMap<List<RouteVariant>, Integer> variantsListIds = new HashMap<List<RouteVariant>, Integer>();
        
        int maxId = 0;

        for (AgencyAndId route : transitIndex.getAllRouteIds()) {
            for (RouteVariant variant : transitIndex.getVariantsForRoute(route)) {
                Geometry accumulatedGeometry = null;
                List<RouteVariant> lastVarSet = null;
                List<Integer> edgeIds = new ArrayList<Integer>();
                geometrySet.edgesByVariant.add(edgeIds);
                List<Edge> edges = variant.getClosestStreetEdges();
                int i = 0;
                for (Edge e : edges) {
                    HashableGeometry g = new HashableGeometry(e.getGeometry());
                    List<RouteVariant> variants = edgeToVariantSet.get(g);
                    Collections.sort(variants);
                    
                    Integer variantListId = variantsListIds.get(variants);
                    if (variantListId == null) {
                        variantListId = maxId++;
                        variantsListIds.put(variants, variantListId);
                        geometrySet.variantSets.add(names(geometrySet.variantNames, variants));
                    }
                    
                    if (variants.equals(lastVarSet) && i != edges.size() - 1) {
                        accumulatedGeometry = accumulatedGeometry.union(g.getGeometry());                        
                    } else {
                        if (accumulatedGeometry != null) {
                            MapUtils.addToMapListUnique(allTrunkGeometry, variants, accumulatedGeometry);
                            HashableGeometry h = new HashableGeometry(accumulatedGeometry);
                            Integer edgeIndex = edgeMap.get(h);
                            if (edgeIndex == null) {
                                edgeIndex = geometrySet.edges.size();
                                geometrySet.edges.add(PolylineEncoder.createEncodings(h.getGeometry()));
                                edgeMap.put(h, edgeIndex);
                                edgeIds.add(edgeIndex);
                                geometrySet.variantSetsByEdge.add(variantListId);
                            }
                        }

                        lastVarSet = variants;
                        accumulatedGeometry = g.getGeometry();
                    }
                    ++i;
                }
            }
        }
        
/*
        
        for (Map.Entry<HashableGeometry, List<RouteVariant>> entry : edgeToVariantSet.entrySet()) {
            HashableGeometry g = entry.getKey();
            List<RouteVariant> variants = entry.getValue();
            Collections.sort(variants);
            Integer id = variantsListIds.get(variants);
            if (id == null) {
                id = maxId++;
                variantsListIds.put(variants, id);
                geometrySet.variantSets.add(names(variants));
            }
            geometrySet.variantSetsByEdge.set(edgeMap.get(g), id);
        }
  */      
        return geometrySet;
    }

    private List<Integer> names(List<String> variantNames, List<RouteVariant> variants) {
        ArrayList<Integer> out = new ArrayList<Integer>();
        for (RouteVariant variant:  variants) {
            String name = variant.getName();
            int index = variantNames.indexOf(name);
            if (index < 0) {
                index = variantNames.size();
                variantNames.add(name);
            }
            out.add(index);
        }
        return out;
    }

}

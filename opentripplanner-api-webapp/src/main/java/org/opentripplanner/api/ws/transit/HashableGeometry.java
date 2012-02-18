package org.opentripplanner.api.ws.transit;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public class HashableGeometry {
    private Geometry geometry;

    HashableGeometry(Geometry g) {
        setGeometry(g);
    }
    
    public boolean equals(Object o) {
        if (!(o instanceof HashableGeometry)) return false;
        HashableGeometry other = (HashableGeometry) o;
        
        if (geometry instanceof LineString) {
            if (!(other.geometry instanceof LineString)) return false;
            return geometry.getCoordinates().equals(other.geometry.getCoordinates());          
        } else if (geometry instanceof MultiLineString) {
            if (!(other.geometry instanceof MultiLineString)) return false;
            for (int i = 0; i < geometry.getNumGeometries(); ++i) {
                if (! geometry.getGeometryN(i).getCoordinates().equals(other.geometry.getGeometryN(i).getCoordinates())) {
                    return false;
                }
            }
            return true;
        }

        //TODO: implement support for other classes 
        throw new UnsupportedOperationException();
    }

    public int hashCode() {
        if (geometry instanceof LineString) {
            return geometry.getCoordinates().hashCode() + 1;          
        } else if (geometry instanceof MultiLineString) {
            int total = 0;
            for (int i = 0; i < geometry.getNumGeometries(); ++i) {
                total += geometry.getGeometryN(i).getCoordinates().hashCode();
            }
            return total;
        }
        //TODO: implement support for other classes
        throw new UnsupportedOperationException();
    }
    
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}

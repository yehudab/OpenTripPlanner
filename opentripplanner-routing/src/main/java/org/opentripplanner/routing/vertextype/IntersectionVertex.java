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

package org.opentripplanner.routing.vertextype;

import org.opentripplanner.routing.graph.Graph;

import com.vividsolutions.jts.geom.Coordinate;

/** Represents an ordinary location in space, typically an intersection */

public class IntersectionVertex extends StreetVertex {

    private static final long serialVersionUID = 1L;

    public IntersectionVertex(Graph g, String label, double x, double y, String name) {
        super(g, label, x, y, name);
    }
    
    public IntersectionVertex(Graph g, String label, double x, double y) {
        super(g, label, x, y, label);
    }

    public IntersectionVertex(Graph g, String label, Coordinate c, String name) {
        super(g, label, c.x, c.y, name);
    }

}
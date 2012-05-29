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
 
 package org.opentripplanner.geocoder.nominatim;
 
 public class NominatimGeocoderAddress {
     private String bus_station;
     private String attraction;
     private String amenity;
     private String house_number;
     private String road;
     private String suburb;
     private String city;
     private String region;
     private String county;
     private String state_district;
     private String state;
     private String postcode;
     private String country;
     private String country_code;
     
     public void setBus_station(String bus_station) {
         this.bus_station = bus_station;
     }

     public String getBus_station() {
         return bus_station;
     }

     public void setAttraction(String attraction) {
         this.attraction = attraction;
     }

     public String getAttraction() {
         return attraction;
     }

     public void setAmenity(String amenity) {
         this.amenity = amenity;
     }

     public String getAmenity() {
         return amenity;
     }

     public void setHouse_number(String house_number) {
         this.house_number = house_number;
     }

     public String getHouse_number() {
         return house_number;
     }

     public void setRoad(String road) {
         this.road = road;
     }

     public String getRoad() {
         return road;
     }

     public void setSuburb(String suburb) {
         this.suburb = suburb;
     }

     public String getSuburb() {
         return suburb;
     }

     public void setCity(String city) {
         this.city = city;
     }

     public String getCity() {
         return city;
     }

     public void setRegion(String region) {
         this.region = region;
     }

     public String getRegion() {
         return region;
     }

     public void setCounty(String county) {
         this.county = county;
     }

     public String getCounty() {
         return county;
     }

     public void setState_district(String state_district) {
         this.state_district = state_district;
     }

     public String getState_district() {
         return state_district;
     }

     public void setState(String state) {
         this.state = state;
     }

     public String getState() {
         return state;
     }

     public void setPostcode(String postcode) {
         this.postcode = postcode;
     }

     public String getPostcode() {
         return postcode;
     }

     public void setCountry(String country) {
         this.country = country;
     }

     public String getCountry() {
         return country;
     }

     public void setCountry_code(String country_code) {
         this.country_code = country_code;
     }

     public String getCountry_code() {
         return country_code;
     }
 }
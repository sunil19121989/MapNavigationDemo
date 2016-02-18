package com.example.mapnavigationdemo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Sai on 2/17/2016.
 */
public class SampleModel {

    public class Sample {
        @SerializedName("name")
        String name;

        public location getLocationObj() {
            return LocationObj;
        }

        public void setLocationObj(location locationObj) {
            LocationObj = locationObj;
        }

        public fromcentral getFromcentralObj() {
            return fromcentralObj;
        }

        public void setFromcentralObj(fromcentral fromcentralObj) {
            this.fromcentralObj = fromcentralObj;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @SerializedName("fromcentral")
        private fromcentral fromcentralObj = null;

        public class fromcentral {
            @SerializedName("car")
            String car;

            public String getTrain() {
                return train;
            }

            public void setTrain(String train) {
                this.train = train;
            }

            public String getCar() {
                return car;
            }

            public void setCar(String car) {
                this.car = car;
            }

            @SerializedName("train")
            String train;
        }

        @SerializedName("location")
        private location LocationObj = null;

        public class location {
            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }

            @SerializedName("latitude")
            String latitude;
            @SerializedName("longitude")
            String longitude;
        }


    }
}

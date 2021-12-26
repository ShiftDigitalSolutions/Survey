package com.shifteg.survey;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SurveyModel implements Parcelable {
    String mobile;
    String category;
    String region;
    int project_id;

    public SurveyModel(String mobile, int project_id, String category, String region) {
        this.mobile = mobile;
        this.category = category;
        this.region = region;
        this.project_id = project_id;
    }


    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private List<Object> message = null;

    protected SurveyModel(Parcel in) {
        mobile = in.readString();
        category = in.readString();
        region = in.readString();
        project_id = in.readInt();
        if (in.readByte() == 0) {
            status = null;
        } else {
            status = in.readInt();
        }
    }

    public static final Creator<SurveyModel> CREATOR = new Creator<SurveyModel>() {
        @Override
        public SurveyModel createFromParcel(Parcel in) {
            return new SurveyModel(in);
        }

        @Override
        public SurveyModel[] newArray(int size) {
            return new SurveyModel[size];
        }
    };

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Object> getMessage() {
        return message;
    }

    public void setMessage(List<Object> message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mobile);
        dest.writeString(category);
        dest.writeString(region);
        dest.writeInt(project_id);
        if (status == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(status);
        }
    }

    public String getMobile() {
        return mobile;
    }

    public String getCategory() {
        return category;
    }

    public String getRegion() {
        return encodeArabic(region);
    }

    public int getProjectId() {
        return project_id;
    }


    private String encodeArabic(String text) {
        try {
            String encoded = java.net.URLEncoder.encode(text, StandardCharsets.UTF_8.name());
            encoded = encoded.replaceAll("\\+", "%20");
            Log.d("SurveyActivityTag", encoded);
            return encoded;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}

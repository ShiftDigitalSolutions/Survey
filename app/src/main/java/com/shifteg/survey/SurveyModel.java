package com.shifteg.survey;

public class SurveyModel {
    String projectId;
    String category;
    String region;
    String mobile;

    public SurveyModel(String projectId, String category, String region, String mobile) {
        this.projectId = projectId;
        this.category = category;
        this.region = region;
        this.mobile = mobile;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getCategory() {
        return category;
    }

    public String getRegion() {
        return region;
    }

    public String getMobile() {
        return mobile;
    }
}

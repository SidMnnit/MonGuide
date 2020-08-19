package com.monguide.monguide.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserDetails {
    private String name;
    private EducationDetails educationDetails;
    private WorkDetails workDetails;

    public UserDetails() {}

    public UserDetails(@NonNull String name, @NonNull UserDetails.EducationDetails educationDetails, @Nullable UserDetails.WorkDetails workDetails) {
        this.name = name;
        this.educationDetails = educationDetails;
        this.workDetails = workDetails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EducationDetails getEducationDetails() {
        return educationDetails;
    }

    public void setEducationDetails(EducationDetails educationDetails) {
        this.educationDetails = educationDetails;
    }

    public WorkDetails getWorkDetails() {
        return workDetails;
    }

    public void setWorkDetails(WorkDetails workDetails) {
        this.workDetails = workDetails;
    }

    // Inner static class for education details
    public static class EducationDetails {
        private String collegeName;
        private String courseName;
        private int graduationYear;

        public EducationDetails() {}

        public EducationDetails(String collegeName, String courseName, int graduationYear) {
            this.collegeName = collegeName;
            this.courseName = courseName;
            this.graduationYear = graduationYear;
        }

        public String getCollegeName() {
            return collegeName;
        }

        public void setCollegeName(String collegeName) {
            this.collegeName = collegeName;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public int getGraduationYear() {
            return graduationYear;
        }

        public void setGraduationYear(int graduationYear) {
            this.graduationYear = graduationYear;
        }
    } // end of EducationDetails

    // Inner static class for work details
    public static class WorkDetails {
        private String companyName;
        private String jobProfile;

        public WorkDetails() {}

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getJobProfile() {
            return jobProfile;
        }

        public void setJobProfile(String jobProfile) {
            this.jobProfile = jobProfile;
        }

        public WorkDetails(String companyName, String jobProfile) {
            this.companyName = companyName;
            this.jobProfile = jobProfile;
        }
    } // End of WorkDetails

}

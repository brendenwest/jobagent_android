package com.brisksoft.jobagent.Classes;

import com.google.gson.annotations.SerializedName;

public class Job {
		private long id;
    public String title;
    public String company;
    public String description;
    @SerializedName("pubdate")
    public String date;
    public String link;
    public String location;
    public String type;
    public String status;
    public String contact;
    public String pay;
	    
	    public Job(){
	        super();
	    }

	    public Job(int id, String title, String company, String description, String link, String location, String type, String date, String status, String contact, String pay) {
	        super();
	        this.id = id;
	        this.title = title;
	        this.company = company;
            this.description = description;
	        this.link = link;
            this.location = location;
            this.date = date;
	        this.type = type;
	        this.status = status;
	        this.contact = contact;
	        this.pay = pay;
	    }

        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }

        public String getCompany() {
            return company;
        }
        public void setCompany(String company) {
            this.company = company;
        }

        public String getLink() {
            return link;
        }
        public void setLink(String link) {
            this.link = link;
        }

        public String getLocation() {
            return location;
        }
        public void setLocation(String location) {
            this.location = location;
        }

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }
        public void setDate(String date) {
            this.date = date;
        }

        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }

        public String getContact() {
            return contact;
        }
        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getPay() {
            return pay;
        }
        public void setPay(String pay) {
            this.pay = pay;
        }

    @Override
	    public String toString() {
	        return this.id + ". " + this.title+ ", " + this.company + ", " + this.description + ", " + this.link + ", " + this.location + ", " +this.type + ", " + this.date + ", " + this.status + ", " + this.contact + ", " + this.pay;
	    }

}
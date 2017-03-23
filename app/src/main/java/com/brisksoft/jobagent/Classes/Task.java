package com.brisksoft.jobagent.Classes;

public class Task {
		private long id;
	    private String title;
	    private String date;
	    private String status;
	    private String priority;
	    private String description;
	    private String company;
	    private String contact;
	    private String job;
	    
	    public Task(){
	        super();
	    }
	    
	    public Task(long id, String title, String date, String status, String priority, String description, String company, String contact, String job) {
	        super();
	        this.id = id;
	        this.title = title;
	        this.date = date;
	        this.status = status;
            this.priority = priority;
            this.description = description;
	        this.company = company;
	        this.contact = contact;
	        this.job = job;
	    }

        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
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

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public String getPriority() {
            return priority;
        }
        public void setPriority(String priority) {
            this.priority = priority;
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

        public String getContact() {
            return contact;
        }
        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getJob() {
            return job;
        }
        public void setJob(String job) {
            this.job = job;
        }


    @Override
	    public String toString() {
	        return this.id + ". " + this.title + ", " + this.date + ", " + this.status + ", " + this.priority + ", " + this.description + ", " + this.company + ", " + this.contact + ", " + this.job;
	    }
}
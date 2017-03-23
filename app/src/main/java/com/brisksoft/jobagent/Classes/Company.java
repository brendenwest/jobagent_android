package com.brisksoft.jobagent.Classes;

public class Company {
		private long id;
	    private String company;
	    private String description;
	    private String type;

	    
	    public Company(){
	        super();
	    }
	    
	    public Company(int id, String company, String description, String type) {
	        super();
	        this.id = id;
	        this.company = company;
            this.description = description;
	        this.type = type;
	    }

        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
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

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }

    @Override
	    public String toString() {
	        return this.id + ", " + this.company + ", " + this.description + ", " + this.type;
	    }
}
package com.brisksoft.jobagent.Classes;

public class Company {
		private long id;
	    private String name;
	    private String description;
	    private String type;

	    public Company(){
	        super();
	    }
	    
	    public Company(String name, String description, String type) {
	        super();
	        this.name = name;
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

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }

    @Override
	    public String toString() {
	        return this.id + ", " + this.name + ", " + this.description + ", " + this.type;
	    }
}
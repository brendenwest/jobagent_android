package com.brisksoft.jobagent.Classes;

public class Contact {
		private long id;
	    private String contact;
	    private String company;
	    private String title;
	    private String phone;
	    private String email;

	    
	    public Contact(){
	        super();
	    }
	    
	    public Contact(int id, String contact, String company, String title, String phone, String email) {
	        super();
	        this.id = id;
	        this.contact = contact;
		    this.company = company;
            this.title = title;
	        this.phone = phone;
            this.email = email;
	    }

        public long getId() {
            return id;
        }
        public void setId(long id) {
            this.id = id;
        }

        public String getContact() {
            return contact;
        }
        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getCompany() {
            return company;
        }
        public void setCompany(String company) {
            this.company = company;
        }

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public String getPhone() {
            return phone;
        }
        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }


    @Override
	    public String toString() {
	        return this.id + ". " + this.contact + ", " + this.company + ", " + this.title + ", " + this.phone + ", " + this.email;
	    }
}
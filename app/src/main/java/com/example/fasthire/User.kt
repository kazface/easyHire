package com.example.fasthire

import java.io.Serializable

class User() : Serializable{

    constructor(email: String) : this() {
        this.email = email;
    }

    constructor(email: String, phone: String, fullName: String, employer: Boolean?) : this() {
        this.email = email;
        this.phone = phone
        this.fullName = fullName
        this.employer = employer
    }
     var id: String? = "";

     var employer: Boolean? = null;

     var email: String = ""

     var phone: String = ""

     var fullName: String = ""

    private var password: String? = ""


}
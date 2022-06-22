package com.example.fasthire

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.core.SnapshotHolder
import com.google.firebase.database.ktx.getValue

class User() {

    constructor(email: String) : this() {
        this.email = email;
    }

    constructor(email: String, phone: String, fullName: String, isEmployer: Boolean) : this() {
        this.email = email;
        this.phone = phone
        this.fullName = fullName
        this.isEmployer = isEmployer
    }

    var id: String = "";
     var isEmployer: Boolean = false;
     var email: String = ""
     var phone: String = ""

     var fullName: String = ""

    private var password: String = ""






}
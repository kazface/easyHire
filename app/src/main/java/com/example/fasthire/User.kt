package com.example.fasthire

class User() {

    constructor(email: String) : this() {
        this.email = email;
    }

    constructor(email: String, phone: String, fullName: String) : this() {
        this.email = email;
        this.phone = phone
        this.fullName = fullName
    }



    final var email: String = ""
    final var phone: String = ""

    final var fullName: String = ""

    private var password: String = ""



}
package com.example.medicaredoctor

class Doctors {

    var name: String? = null
    private var email: String? = null
    private var password: String? = null
    private var uid: String? = null
    private var doctorId: String? = null

    constructor() {}
    constructor(name: String?, email: String?, password: String?, uid: String?, doctorId: String) {
        this.name = name
        this.email = email
        this.password = password
        this.uid = uid
        this.doctorId = doctorId
    }

}
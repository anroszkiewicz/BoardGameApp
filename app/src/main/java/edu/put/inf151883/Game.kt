package edu.put.inf151883

class Game {
    var id: Int = 0
    var name: String? = null
    var year: Int = 0

    constructor(id: Int, name: String, year: Int) {
        this.id = id
        this.name = name
        this.year = year
    }

    constructor(name: String, year: Int) {
        this.name = name
        this.year = year
    }
}
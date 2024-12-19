package com.example.studybuddy.domain

data class Course(
    val courseName: String = "",
    val courseNumber: String = "",
    val department: String = "",
    val academicUnit: String = "",
    val description: String = "",
    val division: String = "",
    val fullCourseCode: String = "",
    val university: String = ""
) {
    companion object {
        fun fromMap(map: Map<String, Any?>): Course {
            return Course(
                courseName = map["courseName"] as? String ?: "",
                courseNumber = map["courseNumber"] as? String ?: "",
                department = map["department"] as? String ?: "",
                academicUnit = map["academicUnit"] as? String ?: "",
                description = map["description"] as? String ?: "",
                division = map["division"] as? String ?: "",
                fullCourseCode = map["fullCourseCode"] as? String ?: "",
                university = map["university"] as? String ?: ""
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "courseName" to courseName,
            "courseNumber" to courseNumber,
            "department" to department,
            "academicUnit" to academicUnit,
            "description" to description,
            "division" to division,
            "fullCourseCode" to fullCourseCode,
            "university" to university
        )
    }
}
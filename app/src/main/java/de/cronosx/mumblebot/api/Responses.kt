package de.cronosx.mumblebot.api

open class BaseResponse (
        val okay: Boolean,
        val message: String
) {}

class RecordingsResponse(
        val okay: Boolean,
        val message: String,
        val data: List<Recording>
) {}

class LabelsResponse(
        val okay: Boolean,
        val message: String,
        val data: List<Label>
) {}

class UsersResponse(
        val okay: Boolean,
        val message: String,
        val data: List<User>
) {}

class SoundsResponse(
        val okay: Boolean,
        val message: String,
        val data: List<Sound>
) {}

package io.github.bkmioa.nexusrss.model


data class MemberInfo(
    val username: String? = null,
    val avatarUrl: String? = null,
    val uid: String? = null,
) {
    companion object {
        val Empty: MemberInfo = MemberInfo()
    }
}
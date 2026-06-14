package me.theclashfruit.rithle.modrinth.enums

enum class Scope(val value: String) {
    UserReadEmail("USER_READ_EMAIL"),
    UserRead("USER_READ"),
    UserWrite("USER_WRITE"),

    NotificationRead("NOTIFICATION_READ"),
    NotificationWrite("NOTIFICATION_WRITE"),

    PayoutsRead("PAYOUTS_READ"),
    PayoutsWrite("PAYOUTS_WRITE"),
    Analytics("ANALYTICS"),

    ProjectCreate("PROJECT_CREATE"),
    ProjectRead("PROJECT_READ"),
    ProjectWrite("PROJECT_WRITE"),
    ProjectDelete("PROJECT_DELETE"),

    VersionCreate("VERSION_CREATE"),
    VersionRead("VERSION_READ"),
    VersionWrite("VERSION_WRITE"),
    VersionDelete("VERSION_DELETE"),

    ReportCreate("REPORT_CREATE"),
    ReportRead("REPORT_READ"),
    ReportWrite("REPORT_WRITE"),
    ReportDelete("REPORT_DELETE"),

    ThreadRead("THREAD_READ"),
    ThreadWrite("THREAD_WRITE"),

    CollectionCreate("COLLECTION_CREATE"),
    CollectionRead("COLLECTION_READ"),
    CollectionWrite("COLLECTION_WRITE"),
    CollectionDelete("COLLECTION_DELETE"),

    OrganizationCreate("ORGANIZATION_CREATE"),
    OrganizationRead("ORGANIZATION_READ"),
    OrganizationWrite("ORGANIZATION_WRITE"),
    OrganizationDelete("ORGANIZATION_DELETE"),
}
/// <reference path="../pb_data/types.d.ts" />

routerAdd("POST", "/revenuecat/webhook", (e) => {
    // Fail-closed: reject all requests if secret is not configured
    const expectedSecret = $os.getenv("REVENUECAT_WEBHOOK_SECRET")
    if (!expectedSecret) {
        throw new ApiError(500, "Webhook secret not configured")
    }

    const authHeader = e.request.header.get("Authorization")
    if (authHeader !== "Bearer " + expectedSecret) {
        throw new UnauthorizedError("Invalid authorization")
    }

    const body = e.requestInfo().body
    const event = body.event

    if (!event) {
        throw new BadRequestError("Missing event data")
    }

    const eventType = event.type
    const appUserId = event.app_user_id

    if (!eventType || !appUserId) {
        throw new BadRequestError("Missing event type or app_user_id")
    }

    const premiumGrantEvents = [
        "INITIAL_PURCHASE",
        "RENEWAL",
        "UNCANCELLATION",
        "NON_RENEWING_PURCHASE",
    ]

    const premiumRevokeEvents = [
        "EXPIRATION",
        "BILLING_ISSUE",
    ]

    let isPremium = null

    if (premiumGrantEvents.includes(eventType)) {
        isPremium = true
    } else if (premiumRevokeEvents.includes(eventType)) {
        isPremium = false
    }

    if (isPremium === null) {
        return e.json(200, { "message": "Event acknowledged, no action taken" })
    }

    try {
        const user = $app.findRecordById("users", appUserId)
        user.set("is_premium", isPremium)
        $app.save(user)
    } catch (err) {
        console.error("RevenueCat webhook: user not found or save failed", appUserId, err)
        throw new NotFoundError("User not found: " + appUserId)
    }

    return e.json(200, { "message": "OK", "is_premium": isPremium })
})

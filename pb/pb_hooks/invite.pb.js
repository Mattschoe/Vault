onRecordAfterCreateSuccess((e) => {
    const invite = e.record;

    const targetEmail = invite.get("to_email");
    const storageID = invite.get("storage_id");

    try {
        const targetUser = $app.findFirstRecordByData("users", "email", targetEmail)
        const storageRecord = $app.findRecordById("storage", storageID)
        const currentUsers = storageRecord.getStringSlice("users")
        if (!currentUsers.includes(targetUser.id)) {
            currentUsers.push(targetUser.id)
            storageRecord.set("users", currentUsers)
            $app.save(storageRecord)
        }
        $app.delete(invite)
        e.next()
    } catch (err) {
        console.error(err)
        throw new BadRequestError("Could not invite user. Check email")
    }
}, "invitations")
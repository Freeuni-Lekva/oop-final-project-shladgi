export async function checkAchievements(userid, action) {
    try {
        const response = await fetch("/achievements", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: new URLSearchParams({
                userid: userid,
                action: action
            })
        });

        const result = await response.json();

        if (result.success) {
            console.log("New achievements unlocked:", result.awarded);
            // You can add animation or tooltip UI here.
        } else {
            console.warn("No achievements awarded:", result.message);
        }
    } catch (error) {
        console.error("Error checking achievements:", error);
    }
}

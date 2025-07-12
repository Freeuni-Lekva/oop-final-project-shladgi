import {getAchievementDiv} from "./achievementDivGetter.js";

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
            const achievementId = result.awarded;
            console.log("New achievement unlocked:", achievementId);
            showAchievementPopup(achievementId);
        } else {
            console.warn("No achievements awarded:", result.message);
        }
    } catch (error) {
        console.error("Error checking achievements:", error);
    }
}

// You need a way to map achievement ids to their display data (icon + title)
const achievementData = {
    1: { title: "First Quiz Created", icon: "/icons/achievement1.png" },
    2: { title: "5 Quizzes Created", icon: "/icons/achievement2.png" },
    3: { title: "10 Quizzes Created", icon: "/icons/achievement3.png" },
    4: { title: "10 Quizzes Taken", icon: "/icons/achievement4.png" },
    5: { title: "Top Scorer", icon: "/icons/achievement5.png" },
    7: { title: "First Quiz Taken", icon: "/icons/achievement7.png" },
    8: { title: "5 Quizzes Taken", icon: "/icons/achievement8.png" },
    9: { title: "50 Quizzes Taken", icon: "/icons/achievement9.png" },
    10: { title: "100 Quizzes Taken", icon: "/icons/achievement10.png" },
};

async function showAchievementPopup(achievementId) {
    const data = achievementData[achievementId];
    if (!data) return;

    const popup = await getAchievementDiv(achievementId);
    popup.style.position = "fixed";
    popup.style.bottom = "20px";
    popup.style.right = "20px";
    popup.style.background = "rgba(0,0,0,0.8)";
    popup.style.color = "white";
    popup.style.padding = "15px";
    popup.style.borderRadius = "8px";
    popup.style.display = "flex";
    popup.style.alignItems = "center";
    popup.style.boxShadow = "0 0 10px rgba(0,0,0,0.5)";
    popup.style.zIndex = 10000;

    document.body.appendChild(popup);

    setTimeout(() => {
        popup.remove();
    }, 4000);
}


function getRarityColor(rarity) {
    switch (rarity) {
        case "Common": return "secondary";
        case "Rare": return "primary";
        case "Epic": return "warning";
        case "Legendary": return "danger";
        default: return "dark";
    }
}

export async function getAchievementDiv(achievementId) {
    try {
        const response = await fetch("achievement", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            body: `id=${encodeURIComponent(achievementId)}`
        });

        if (!response.ok) {
            throw new Error(`HTTP error ${response.status}`);
        }

        const achievement = await response.json();

        const div = document.createElement("div");
        div.className = "achievement-div alert alert-secondary mb-3 d-flex align-items-center";

        const icon = document.createElement("img");
        icon.src = achievement.iconLink;
        icon.alt = "Achievement Icon";
        icon.className = "me-3";
        icon.style.width = "64px";
        icon.style.height = "64px";


        const content = document.createElement("div");

        const title = document.createElement("h5");
        title.textContent = achievement.title;

        const desc = document.createElement("p");
        desc.textContent = achievement.description;

        const rarity = document.createElement("span");
        rarity.textContent = `Rarity: ${achievement.rarity}`;
        rarity.className = `badge bg-${getRarityColor(achievement.rarity)}`;

        content.appendChild(title);
        content.appendChild(desc);
        content.appendChild(rarity);

        div.appendChild(icon);
        div.appendChild(content);

        return div;
    } catch (error) {
        console.error("Failed to fetch achievement:", error);
        const errorDiv = document.createElement("div");
        errorDiv.className = "alert alert-danger";
        errorDiv.textContent = "Failed to load achievement.";
        return errorDiv;
    }
}

export async function loadSessionValue(key) {
    try {
        const response = await fetch(`/session-info?key=${encodeURIComponent(key)}`);

        const data = await response.json();
        console.log(key +" "+ data.value);
        return data.value;
    } catch (err) {
        console.error("Error:", err);
        return null;
    }
}




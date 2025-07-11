export async function loadSessionValue(key) {
    try {
        const response = await fetch(`/session-info?key=${encodeURIComponent(key)}`);

        const data = await response.json();

        return data.value;
    } catch (err) {

        return null;
    }
}




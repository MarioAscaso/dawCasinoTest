const CONFIG = {
    API_URL: 'http://localhost:8888/api' 
};

async function handleErrors(response) {
    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || response.statusText);
    }
    return response;
}
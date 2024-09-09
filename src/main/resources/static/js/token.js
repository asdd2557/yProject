const token = searchParam('token')

if (token) {
    localStorage.setItem("access_token", token)
    console.log("token.js: ", token );
}

function searchParam(key) {
    return new URLSearchParams(location.search).get(key);
}
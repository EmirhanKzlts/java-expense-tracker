import axios from "axios";

const api = axios.create({

    baseURL: import.meta.env.VITE_BACKEND_URL,
});

api.interceptors.request.use((config) => {

    const token = localStorage.getItem("JWT_TOKEN");

    if (token) {
        config.headers.Authorization = `Bearer ${JSON.parse(token)}`;
    }
    return config;

});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem("JWT_TOKEN");
            localStorage.removeItem("USER");

            window.location.href = "/login";
        }

        return Promise.reject(error);
    }
)

export default api;
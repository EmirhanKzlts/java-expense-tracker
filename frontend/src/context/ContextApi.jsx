import { createContext, useContext, useState } from "react";

const ContextApi = createContext();

const isTokenExpired = (token) => {
    if (!token) return true;

    try {
        const payload = JSON.parse(atob(token.split(".")[1]));

        return payload.exp * 1000 < Date.now();
    } catch {
        return true;
    }
};

export const ContextProvider = ({ children }) => {

    const storedToken = localStorage.getItem("JWT_TOKEN")
        ? JSON.parse(localStorage.getItem("JWT_TOKEN"))
        : null;

    const storedUser = localStorage.getItem("USER")
        ? JSON.parse(localStorage.getItem("USER"))
        :null;

    const validToken = isTokenExpired(storedToken)
        ? null
        : storedToken;

    if (!validToken && storedToken) {
        localStorage.removeItem("JWT_TOKEN");
        localStorage.removeItem("USER");
    }

    const [token, setToken] = useState(validToken);
    const [user, setUser] = useState(
        validToken ? storedUser : null
    );

    const logout = () => {
        localStorage.removeItem("JWT_TOKEN");
        localStorage.removeItem("USER");

        setToken(null);
        setUser(null);
    }

    const sendData = {
        token,
        setToken,
        user,
        setUser,
        logout
    };



    return <ContextApi.Provider value={sendData}>{children}</ContextApi.Provider>
};

export const useStoreContext = () => {
    return  useContext(ContextApi);
}
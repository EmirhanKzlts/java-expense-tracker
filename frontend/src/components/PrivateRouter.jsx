import {useStoreContext} from "../context/ContextApi.jsx";
import {Navigate} from "react-router-dom";

export default function PrivateRouter({ children, publicPage }) {

    const { token } = useStoreContext();

    if (publicPage) {
        return token
            ? <Navigate to="/dashboard" />
            : children;
    }

    return !token
        ? <Navigate to="/login" />
        :children;
}
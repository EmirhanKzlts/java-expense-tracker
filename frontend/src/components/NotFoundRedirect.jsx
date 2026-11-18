import {useStoreContext} from "../context/ContextApi.jsx";
import {Navigate} from "react-router-dom";

const NotFoundRedirect = () => {

    const {token} = useStoreContext();

    return (
        <Navigate to={token ? "/dashboard" : "/login"}
                  replace
        />
    )
}

export default NotFoundRedirect;
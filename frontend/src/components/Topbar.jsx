import {useStoreContext} from "../context/ContextApi.jsx";
import {Bell, Search} from "lucide-react";

const greeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return "Good morning";
    if (hour < 18) return "Good afternoon";
    return "Good evening";
};

const formatToday = () => {
    return  new Date().toLocaleDateString("en-US", {
        weekday: "long",
        month: "long",
        day: "numeric",
    });
}

const Topbar = () => {

    const { user } = useStoreContext();
    const username = user?.username;

    return (
        <header className="h-16 bg-white border-b border-slate-100 flex items-center justify-between px-6 shrink-0">

            <div>
                <div className="text-sm font-semibold text-slate-900 tracking-tight">
                    {greeting()} {username && `, ${username.charAt(0).toUpperCase()}${username.slice(1)}`} 👋
                </div>
                <div className="text-xs text-slate-500">{formatToday()}</div>
            </div>

            <div className="flex items-center gap-1">
                <button
                title="Search"
                className="h-9 w-9 rounded-full text-slate-500 hover:bg-slate-100 hover:text-slate-900 flex items-center justify-center transition"
                >
                    <Search size={17}/>
                </button>

                <button
                title="Notifications"
                className="relative h-9 w-9 rounded-full text-slate-500 hover:bg-slate-100 flex items-center justify-center transition"
                >
                    <Bell size={17} />
                    <span className="absolute top-2 right-2 h-2 w-2 bg-rose-500 rounded-full ring-2 ring-white"/>
                </button>

            </div>

        </header>
    )
}
export default Topbar

import {Link, useNavigate} from "react-router-dom";
import {useState} from "react";
import {ChevronDown, Eye, EyeOff} from "lucide-react";
import Spinner from "../components/Spinner.jsx"
import api from "../assets/api.js";

const CURRENCIES = [
    { value: 'USD', label: 'USD - US Dollar' },
    { value: 'EUR', label: 'EUR - Euro' },
    { value: 'GBP', label: 'GBP - British Pound' },
    { value: 'INR', label: 'INR - Indian Rupee' },
    { value: 'JPY', label: 'JPY - Japanese Yen' },
    { value: 'CAD', label: 'CAD - Canadian Dollar' },
    { value: 'TRY', label: 'TRY - Turkish Lira' },
];

const Register = () => {

    const onSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            await api.post("/api/auth/register", form)
            navigate("/login")
        } catch (error) {
            console.log(error)
        } finally {
            setLoading(false);
        }
    }

    const navigate = useNavigate();

    const [form, setForm] = useState({
        username:"",
        email:"",
        password:"",
        currency:"USD",
    });

    const [loading, setLoading] = useState(false);

    const [showPassword, setShowPassword] = useState(false);

    return (

        <div className="bg-gradient-to-r from-purple-50 to-indigo-50 opacity-80 min-h-screen flex justify-center items-center">

            <div className="w-full max-w-sm rounded-2xl bg- p-6 border-2 border-transparent border-violet-500 shadow-lg bg-white">
                <h1 className="text-center font-bold lg:text-3xl mb-6">
                    Sign Up Here
                </h1>

                <form className="space-y-5" onSubmit={onSubmit}>

                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 ml-5">Name</label>
                        <input
                            required
                            value={form.username}
                            onChange={(e) => setForm({...form, username: e.target.value})}
                            className="w-full bg-slate-200 hover:bg-slate-200/70 focus:bg-white border-2 border-transparent focus:border-violet-500 rounded-2xl px-5 py-4 text-slate-900 text-sm focus:outline-none transition"
                            placeholder="Alex"
                        />
                    </div>


                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 ml-5">Email</label>
                        <input
                            required
                            type="email"
                            value={form.email}
                            onChange={(e) => setForm({...form, email: e.target.value})}
                            className="w-full bg-slate-200 hover:bg-slate-200/70 focus:bg-white border-2 border-transparent focus:border-violet-500 rounded-2xl px-5 py-4 text-slate-900 text-sm focus:outline-none transition"
                            placeholder="example@mail.com"
                        />
                    </div>


                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 ml-5">Password</label>
                        <div className="relative">
                            <input
                                type={showPassword ? "text" : "password"}
                                required
                                minLength={6}
                                value={form.password}
                                onChange={(e) => setForm({...form, password: e.target.value})}
                                className="w-full bg-slate-200 hover:bg-slate-200/70 focus:bg-white border-2 border-transparent focus:border-violet-500 rounded-2xl px-5 py-4 text-slate-900 text-sm focus:outline-none transition"
                                placeholder="At least 6 characters"
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword((v) => !v)}
                                className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 transition"
                                tabIndex={-1}
                            >
                                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                            </button>
                        </div>
                    </div>


                    <div className="space-y-2">
                        <label className="text-sm font-semibold text-slate-700 ml-5">Currency</label>
                        <div className="relative">
                            <select
                                value={form.currency}
                                onChange={(e) => setForm({...form, currency: e.target.value})}
                                className="w-full appearance-none bg-slate-200 hover:bg-slate-200/70 focus:bg-white border-2 border-transparent focus:border-violet-500 rounded-2xl px-5 py-4 pr-12 text-slate-900 text-sm focus:outline-none transition cursor-pointer"
                            >
                                {CURRENCIES.map((currency) => (
                                    <option key={currency.value} value={currency.value}>
                                        {currency.label}
                                    </option>
                                ))}
                            </select>
                            <ChevronDown
                                size={18}
                                className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none"
                            />

                        </div>
                    </div>


                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full inline-flex items-center justify-center gap-2 bg-linear-to-br from-violet-400 to-violet-600 active:bg-violet-800 text-white font-semibold py-4 rounded-2xl transition shadow-lg shadow-violet-500/30 disabled:opacity-60 disabled:cursor-not-allowed"
                    >
                        {loading ? (
                            <>
                                <Spinner size="sm"/>
                                Creating account...
                            </>
                        ) : (
                            "Create Account"
                        )}
                    </button>


                </form>

                <p className="text-center mt-8 text-md text-slate-600">
                    Already have an account?{" "}
                    <Link className="text-violet-500 font-semibold hover:text-violet-600 transition"
                          to="/login"
                    >
                        Sign in
                    </Link>
                </p>

            </div>

        </div>
    )
}

export default Register;
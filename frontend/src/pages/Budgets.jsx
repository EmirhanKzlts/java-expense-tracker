import {AlertOctagon, AlertTriangle, CheckCircle2, Pencil, Plus, Trash2, Target} from "lucide-react";
import {useStoreContext} from "../context/ContextApi.jsx";
import {useEffect, useState} from "react";
import api from "../assets/api.js";
import Button from "../components/ui/Button.jsx";
import Spinner from "../components/Spinner.jsx";
import EmptyState from "../components/EmptyState.jsx";
import CategoryBadge from "../components/CategoryBadge.jsx";
import {formatCurrency} from "../utils/format.js";
import Modal from "../components/ui/Modal.jsx";
import BudgetForm from "../components/BudgetForm.jsx";

const statusStyles = {
    good: {
        Icon: CheckCircle2,
        label: 'On Track',
        bg: 'bg-emerald-50',
        text: 'text-emerald-700',
        iconColor: 'text-emerald-600',
    },
    caution: {
        Icon: AlertTriangle,
        label: 'Watch It',
        bg: 'bg-amber-50',
        text: 'text-amber-700',
        iconColor: 'text-amber-600',
    },
    concerning: {
        Icon: AlertOctagon,
        label: 'Over Budget',
        bg: 'bg-rose-50',
        text: 'text-rose-700',
        iconColor: 'text-rose-600',
    },
};

const Budgets = () =>{
    const { user } = useStoreContext();
    const currency = user?.currency || "USD";
    const [budgets, setBudgets] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [modalOpen, setModalOpen] = useState(false);
    const [editing, setEditing] = useState(null);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [bRes, cRes] = await Promise.all([
                api.get("/api/budgets"),
                api.get("/api/categories"),
            ]);
            setBudgets(bRes.data);
            setCategories(cRes.data);
        } catch (error) {
            console.log(error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const onEdit = (budget) => {
        setEditing(budget);
        setModalOpen(true);
    };

    const onCreate = () => {
        setEditing(null);
        setModalOpen(true);
    };

    const onDelete = async (id) => {
        if (!confirm('Delete this budget?')) return;
        try {
            await api.delete(`/api/budgets/${id}`)
            fetchData();
        } catch (error) {
            console.log(error);
        }
    };

    const onSaved = () => {
        setModalOpen(false);
        fetchData();
    };

    return(
        <div className="space-y-6">
            <div className="flex items-center justify-between gap-3">
                <div>
                    <h1 className="text-3xl font-bold text-slate-900 tracking-tight">Budgets</h1>
                    <p className="text-sm text-slate-500 mt-1.5">
                        Set spending limits per category
                    </p>
                </div>
                <div>
                    <Button onClick={onCreate}>
                        <Plus size={16} /> Add Budget
                    </Button>
                </div>
            </div>

            {loading ? (
                <div className="flex justify-center py-12">
                    <Spinner />
                </div>
            ) : budgets.length === 0 ? (
                <EmptyState
                    icon={Target}
                    title="No budgets yet"
                    description="Create a budget to track spending limits."
                />
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                    {budgets.map((budget) => {
                        const spent = parseFloat(budget.spent);
                        const total = parseFloat(budget.amount);
                        const pct = total > 0 ? Math.min((spent / total) * 100, 100) : 0;
                        const over = spent > total;
                        const barColor =
                            pct >= 100 ? 'bg-rose-500' : pct >= 70 ? 'bg-amber-500' : 'bg-emerald-500'

                        return (
                            <div
                                key={budget.id}
                                className="bg-white rounded-3xl border border-slate-100 p-6 hover:border-slate-200 transition"
                            >
                                <div className="flex items-start justify-between mb-5">
                                    <CategoryBadge
                                        name={budget.categoryName}
                                        icon={budget.categoryIcon}
                                        color={budget.categoryColor}
                                    />
                                    <div className="flex items-center gap-1">
                                        <button
                                            onClick={() => onEdit(budget)}
                                            className="p-1.5 hover:bg-slate-100 rounded-md text-slate-500 transition"
                                        >
                                            <Pencil size={14} />
                                        </button>
                                        <button
                                            onClick={() => onDelete(budget.id)}
                                            className="p-1.5 hover:bg-rose-50 rounded-md text-slate-500 transition"
                                        >
                                            <Trash2 size={14} />
                                        </button>
                                    </div>
                                </div>
                                <div className="mb-3 flex items-baseline justify-between">
                                    <span className="text-3xl font-bold tracking-tight text-slate-900">
                                        {formatCurrency(spent, currency)}
                                    </span>
                                    <span className="text-sm text-slate-500">
                                        of {formatCurrency(total, currency)}
                                    </span>
                                </div>
                                <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                                    <div
                                        className={`h-full ${barColor} transition-all`}
                                        style={{ width: `${pct}%`}}
                                    />
                                </div>
                                <div className="mt-2.5 flex items-center justify-between text-xs">
                                    <span className="text-slate-500 capitalize">{budget.period} · {pct.toFixed(0)}% used</span>
                                    <span>
                                        {over
                                            ? `Over by ${formatCurrency(spent - total, currency)}`
                                            : `${formatCurrency(total - spent, currency)} left`}
                                    </span>
                                </div>

                            </div>
                        )
                    })}
                </div>
            )}

            <Modal
                open={modalOpen}
                onClose={() => setModalOpen(false)}
                title={editing ? 'Edit Budget' : 'New Budget'}
            >
                <BudgetForm
                    initial={editing}
                    categories={categories.filter((category) => category.type === 'EXPENSE')}
                    onSaved={onSaved}
                    onCancel={() => setModalOpen(false)}
                />
            </Modal>

        </div>
    )
}

export default Budgets;
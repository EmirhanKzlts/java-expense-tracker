import {useStoreContext} from "../context/ContextApi.jsx";
import {useEffect, useMemo, useState} from "react";
import api from "../assets/api.js";
import Button from "../components/ui/Button.jsx";
import {Pencil, Plus, Search, Trash2, Wallet, ChevronLeft, ChevronRight} from "lucide-react";
import TransactionTrendChart from "../components/charts/TransactionTrendChart.jsx";
import EmptyState from "../components/EmptyState.jsx";
import Spinner from "../components/Spinner.jsx";
import CategoryBadge from "../components/CategoryBadge.jsx";
import {formatCurrency, formatDate} from "../utils/format.js";
import StatusPill from "../components/StatusPill.jsx";
import Modal from "../components/ui/Modal.jsx";
import TransactionForm from "../components/TransactionForm.jsx";

const Transactions = () => {

    const { user } = useStoreContext();
    const currency = user?.currency || "USD";
    const [allTransactions, setAllTransactions] = useState([]);
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState({ search: '', type: '', categoryId: ''});
    const [modalOpen, setModalOpen] = useState(false);
    const [editing, setEditing] = useState(null);
    const [timeRange, setTimeRange] = useState('monthly');
    const [page, setPage] = useState(1);
    const PAGE_SIZE = 20;

    const fetchData = async () => {
        const params = { limit: 2000 };

        try {
            setLoading(true);
            const [tRes, cRes] = await Promise.all([
                api.get("/api/transactions", { params }),
                api.get("/api/categories"),
            ]);
            setAllTransactions(tRes.data);
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

    useEffect(() => {
        setPage(1)
    }, []);

    const transactions = useMemo(() => {
        return allTransactions.filter((transaction) => {

            const matchesSearch =
                !filters.search ||
                transaction.description
                    ?.toLowerCase()
                    .includes(filters.search.toLowerCase());

            const matchesType =
                !filters.type ||
                transaction.type === filters.type;

            const matchesCategory =
                !filters.categoryId ||
                transaction.categoryId === Number(filters.categoryId);

            return matchesSearch && matchesType && matchesCategory;
        });
    },[allTransactions, filters.search, filters.type, filters.categoryId]);

    const counts = useMemo(
        () => ({
            all: allTransactions.length,
            income: allTransactions.filter((transaction) => transaction.type === "INCOME").length,
            expense: allTransactions.filter((transaction) => transaction.type === "EXPENSE").length,
        }),
        [allTransactions]
    );

    const MONTH_NAMES = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    const trendData = useMemo(() => {
        const now = new Date();
        const txnKey = (transaction) => (transaction.transactionDate || '').split('T')[0];
        const addAmount = (entry, transaction) => {
            const amount = parseFloat(transaction.amount);
            if (transaction.type === "INCOME") entry.income += amount;
            else entry.expense += amount;
        };

        if (timeRange === '30d' || timeRange === '3m') {
            const totalDays = timeRange === '30d' ? 30 : 90;
            const buckets = [];
            for (let i = totalDays - 1; i >= 0; i--) {
                const d = new Date(now.getFullYear(), now.getMonth(), now.getDate() - i);
                const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
                const label = `${d.getMonth() + 1}/${d.getDate()}`;
                buckets.push({ key, label, income: 0, expense: 0 });
            }
            const map = new Map(buckets.map((b) => [b.key, b]));
            allTransactions.forEach((t) => {
                const entry = map.get(txnKey(t));
                if (entry) addAmount(entry, t);
            });
            return buckets;
        }

        if (timeRange === 'monthly') {
            const buckets = [];
            for (let i = 11; i >= 0; i--) {
                const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
                const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
                const label = `${MONTH_NAMES[d.getMonth()]} ${String(d.getFullYear()).slice(2)}`;
                buckets.push({ key, label, income: 0, expense: 0 });
            }
            const map = new Map(buckets.map((b) => [b.key, b]));
            allTransactions.forEach((t) => {
                const [year, month] = txnKey(t).split('-');
                const entry = map.get(`${year}-${month}`);
                if (entry) addAmount(entry, t);
            });
            return buckets;
        }

        if (timeRange === 'yearly') {
            const buckets = [];
            for (let i = 4; i >= 0; i--) {
                const y = String(now.getFullYear() - i);
                buckets.push({ key: y, label: y, income: 0, expense: 0 });
            }
            const map = new Map(buckets.map((b) => [b.key, b]));
            allTransactions.forEach((transaction) => {
                const year = txnKey(transaction).split('-')[0];
                const entry = map.get(year);
                if (entry) addAmount(entry, transaction)
            });
            return buckets;
        }

        return [];
    }, [allTransactions, timeRange]);

    const chartInterval = timeRange === '30d' ? 3 : timeRange === '3m' ? 10 : 0;

    const totalPages = Math.max(1, Math.ceil(transactions.length / PAGE_SIZE));
    const safePage = Math.min(page, totalPages);
    const startIdx = (safePage - 1) * PAGE_SIZE;
    const paginated = transactions.slice(startIdx, startIdx + PAGE_SIZE);

    const getPageNumbers = () => {
        if (totalPages <= 7) {
            return Array.from({ length: totalPages }, (_, i) => i + 1);
        }
        if (safePage <= 4) return [1, 2, 3, 4, 5, '...', totalPages];
        if (safePage >= totalPages - 3) {
            return [1, '...', totalPages - 4, totalPages - 3, totalPages - 2, totalPages - 1, totalPages - 2];
        }
        return [1, '...', safePage - 1, safePage, safePage + 1, '...', totalPages];
    };

    const onEdit = (transaction) => {
        setEditing(transaction);
        setModalOpen(true);
    };

    const onCreate = () => {
        setEditing(null);
        setModalOpen(true);
    };

    const onDelete = async (id) => {
        if (!confirm('Delete this transaction?')) return;
        try {
            await api.delete(`/api/transactions/${id}`);
            fetchData();
        } catch (error) {
            console.error(error);
        }
    };

    const onSaved = () => {
        setModalOpen(false);
        fetchData();
    };

    const tabs = [
        { value: '', label: 'All', count: counts.all, badge: 'bg-slate-200 text-slate-700' },
        { value: 'INCOME', label: 'Income', count: counts.income, badge: 'bg-emerald-100 text-emerald-700' },
        { value: 'EXPENSE', label: 'Expense', count: counts.expense, badge: 'bg-rose-100 text-rose-700' },
    ];


    return(
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold text-slate-900 tracking-tight">Transactions</h1>
                    <p className="text-sm text-slate-500 mt-1.5">All your income and expenses</p>
                </div>
                <Button onClick={onCreate}>
                    <Plus size={16} /> Add Transaction
                </Button>
            </div>

            <div className="bg-white rounded-3xl border border-slate-100 p-6">
                <div className="mb-5 flex items-center justify-between gap-3">
                    <div>
                        <h2 className="text-lg font-bold text-slate-900 tracking-tight">Transaction Trend</h2>
                        <p className="text-xs text-slate-500 mt-1">Income vs expenses over time</p>
                    </div>
                    <div className="flex items-center gap-1 bg-slate-100 p-1 rounded-full shrink-0">
                        {[
                            { value: '30d', label: '30D' },
                            { value: '3m', label: '3M' },
                            { value: 'monthly', label: 'Monthly' },
                            { value: 'yearly', label: 'Yearly' },
                        ].map((r) => (
                            <button
                                key={r.value}
                                onClick={() => setTimeRange(r.value)}
                                className={`px-3 py-1 rounded-full text-xs font-medium transition ${
                                    timeRange === r.value
                                        ? 'bg-white shadow-sm text-slate-900'
                                        : 'text-slate-600 hover:text-slate-900'
                                }`}
                            >
                                {r.label}
                            </button>
                        ))}
                    </div>
                </div>
                <TransactionTrendChart data={trendData} currency={currency} interval={chartInterval} />
            </div>

            <div className="bg-white rounded-3xl border border-slate-100 p-5">
                <div className="flex flex-col lg:flex-row lg:items-center gap-3 mb-5">
                    <div className="relative flex-1">
                        <Search size={16} className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" />
                        <input
                            value={filters.search}
                            onChange={(e) => setFilters({ ...filters, search: e.target.value })}
                            placeholder="Search description"
                            className="w-full pl-10 pr-4 py-2 rounded-full border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-violet-500 focus:border-transparent"
                        />
                    </div>

                    <div className="flex items-center gap-1 bg-slate-100 p-1 rounded-full self-start lg:self-auto">
                        {tabs.map((tab) => (
                            <button
                                key={tab.value || 'all'}
                                onClick={() => setFilters({ ...filters, type: tab.value })}
                                className={`px-4 py-1.5 rounded-full text-sm font-medium transition flex items-center gap-2 ${
                                    filters.type === tab.value
                                        ? 'bg-white shadow-sm text-slate-900'
                                        : 'text-slate-600 hover:text-slate-900'
                                }`}
                            >
                                {tab.label}
                                <span className={`text-xs px-1.5 py-0.5 rounded-full font-semibold ${tab.badge}`}>
                                    {tab.count}
                                </span>
                            </button>
                        ))}
                    </div>

                    <select
                        value={filters.categoryId}
                        onChange={(e) => setFilters({ ...filters, categoryId: e.target.value })}
                        className="px-4 py-2 rounded-full border border-slate-200 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-violet-500"
                    >
                        <option value="">All categories</option>
                        {categories.map((category) => (
                            <option key={category.id} value={category.id}>
                                {category.categoryName}
                            </option>
                        ))}
                    </select>
                </div>

                {loading ? (
                    <div className="flex justify-center py-12">
                        <Spinner/>
                    </div>
                ) : transactions.length === 0 ? (
                    <EmptyState
                        icon={Wallet}
                        title="No transactions"
                        description="Try adjusting filters, or add a new transaction."
                        action={
                            <Button>
                                <Plus size={16} /> Add transaction
                            </Button>
                        }
                    />
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead>
                                <tr className="text-left text-xs font-semibold text-slate-500 uppercase tracking-wider border-b border-slate-100">
                                    <th className="pb-4 pr-4">Category</th>
                                    <th className="pb-4 pr-4">Description</th>
                                    <th className="pb-4 pr-4">Date</th>
                                    <th className="pb-4 pr-4">Type</th>
                                    <th className="pb-4 pr-4 text-right">Amount</th>
                                    <th className="pb-4"></th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-100">
                                {paginated.map(transaction => (
                                    <tr key={transaction.id} className="hover:bg-slate-50/60 transition">
                                        <td className="py-4 pr-4">
                                            <CategoryBadge
                                                name={transaction.categoryName || 'Uncategorized'}
                                                icon={transaction.icon}
                                                color={transaction.color}
                                                size="sm"
                                            />
                                        </td>
                                        <td className="py-4 pr-4 text-sm text-slate-700">
                                            {transaction.description || '—'}
                                        </td>
                                        <td className="py-4 pr-4 text-sm text-slate-500 whitespace-nowrap">
                                            {formatDate(transaction.transactionDate)}
                                        </td>
                                        <td className="py-4 pr-4">
                                            <StatusPill variant={transaction.type === 'INCOME' ? 'INCOME' : 'EXPENSE'}>
                                                {transaction.type}
                                            </StatusPill>
                                        </td>
                                        <td
                                            className={`py-4 pr-4 text-sm font-semibold text-right whitespace-nowrap ${
                                                transaction.type === 'INCOME' ? 'text-emerald-600' : 'text-rose-600'
                                            }`}
                                        >
                                            {transaction.type === 'INCOME' ? '+' : '-'}
                                            {formatCurrency(transaction.amount, currency)}
                                        </td>
                                        <td className="ğy-4 text-right">
                                            <div className="flex items-center justify-end gap-1">
                                                <button
                                                    onClick={() => onEdit(transaction)}
                                                    className="p-1.5 hover:bg-slate-100 rounded-md text-slate-500 transition"
                                                >
                                                    <Pencil size={14} />
                                                </button>
                                                <button
                                                    onClick={() => onDelete(transaction.id)}
                                                    className="p-1.5 hover:bg-rose-50 rounded-md text-rose-500 transition"
                                                >
                                                    <Trash2 size={14} />
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>

                        {totalPages > 1 && (
                            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3 mt-5 pt-5 border-t border-slate-100">
                                <div className="text-xs text-slate-500">
                                    Showing{' '}
                                    <span className="font-semibold text-slate-700">
                                        {startIdx + 1}-{Math.min(startIdx + PAGE_SIZE, transactions.length)}
                                    </span>{' '}
                                    of{' '}
                                    <span className="font-semibold text-slate-700">{transactions.length}</span>
                                </div>
                                <div className="flex items-center gap-1">
                                    <button
                                        onClick={() => setPage((page) => Math.max(1, page - 1))}
                                        disabled={safePage === 1}
                                        className="h-8 w-8 flex items-center justify-center rounded-lg text-slate-600 hover:bg-slate-100 disabled:opacity-40 disabled:cursor-not-allowed transition"
                                    >
                                        <ChevronLeft size={16} />
                                    </button>
                                    {getPageNumbers().map((page, i) =>
                                        page === "..." ? (
                                            <span key={`gap-${i}`} className="px-1.5 text-slate-400 text-sm">
                                                {page}
                                            </span>
                                        ) : (
                                            <button
                                                key={page}
                                                onClick={() => setPage(page)}
                                                className={`h-8 min-w-8 px-2.5 rounded-lg text-sm font-medium transition ${
                                                    safePage === page
                                                        ? 'bg-violet-600 text-white shadow-sm shadow-violet-500/30'
                                                        : 'text-slate-600 hover:bg-slate-100'
                                                }`}
                                            >
                                                {page}
                                            </button>
                                        )
                                    )}
                                    <button
                                        onClick={() => setPage((page) => Math.min(totalPages, page + 1))}
                                        disabled={safePage === totalPages}
                                        className="h-8 w-8 flex items-center justify-center rounded-lg text-slate-600 hover:bg-slate-100 disabled:opacity-40 disabled:cursor-not-allowed transition"
                                    >
                                        <ChevronRight size={16} />
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                )}
            </div>

            <Modal
                open={modalOpen}
                onClose={() => setModalOpen(false)}
                title={editing ? 'Edit Transaction' : 'New Transaction'}
            >
                <TransactionForm
                    inital={editing}
                    categories={categories}
                    onSaved={onSaved}
                    onCancel={() => setModalOpen(false)}
                />
            </Modal>

        </div>
    )
}

export default Transactions;
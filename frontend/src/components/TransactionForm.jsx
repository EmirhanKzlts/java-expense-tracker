import {useState} from "react";
import {todayDateString} from "../utils/format.js";
import api from "../assets/api.js";
import Input from "./ui/Input.jsx";
import Select from "./ui/Select.jsx";
import Button from "./ui/Button.jsx";

const TransactionForm = ({ initial, categories, onSaved, onCancel }) => {
    const [form, setForm] = useState({
        type: initial?.type || 'EXPENSE',
        amount: initial?.amount || '',
        categoryId: initial?.categoryId || '',
        description: initial?.description || '',
        transactionDate: initial?.transactionDate?.split('T')[0] || todayDateString(),
    });
    const [saving, setSaving] = useState(false);

    const filteredCategories = categories.filter((category) => category.type === form.type);

    const submit = async (e) => {
        e.preventDefault();
        setSaving(true);
        const now = new Date();
        const transactionDateTime = `${form.transactionDate}T${now.toTimeString().split(' ')[0]}`;
        try {

            const payload = {
                type: form.type,
                amount: parseFloat(form.amount),
                categoryId: Number(form.categoryId),
                description: form.description || null,
                transactionDate: transactionDateTime,
            };
            if (initial) {
                // UPDATE
                await api.put(`/api/transactions/${initial.id}`, payload);
            } else {
                // CREATE
                await api.post('/api/transactions', payload);
            }
            onSaved();
        } catch (error) {
            console.error(error);
        } finally {
            setSaving(false);
        }
    };

    return (
        <form onSubmit={submit} className="space-y-4">
            <div className="grid grid-cols-2 gap-2">
                <button
                    type="button"
                    onClick={() => setForm({ ...form, type: 'EXPENSE', categoryId: '' })}
                    className={`py-2 px-4 rounded-lg text-sm font-medium transition ${
                        form.type === 'EXPENSE'
                            ? 'bg-rose-500 text-white'
                            : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                >
                    Expense
                </button>
                <button
                    type="button"
                    onClick={() => setForm({ ...form, type: 'INCOME', categoryId: '' })}
                    className={`py-2 px-4 rounded-lg text-sm font-medium transition ${
                        form.type === 'INCOME'
                            ? 'bg-emerald-500 text-white'
                            : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                    }`}
                >
                    Income
                </button>
            </div>

            <Input
                label="Amount"
                type="number"
                step="0.01"
                min="0.01"
                required
                value={form.amount}
                onChange={(e) => setForm({ ...form, amount: e.target.value })}
            />

            <Select
                label="Category"
                value={form.categoryId}
                onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
                required
            >
                <option value="">Select a category</option>
                {filteredCategories.map((category) => (
                    <option key={category.id} value={category.id}>
                        {category.categoryName}
                    </option>
                ))}
            </Select>

            <Input
                label="Description"
                placeholder="e.g. Coffe at Starbucks"
                value={form.description}
                onChange={(e) => setForm({...form, description: e.target.value })}
            />

            <Input
                label="Date"
                type="date"
                required
                value={form.transactionDate}
                onChange={(e) => setForm({...form, transactionDate: e.target.value })}
            />

            <div className="flex gap-2 justify-end pt-2">
                <Button type="button" variant="outline" onClick={onCancel}>
                    Cancel
                </Button>
                <Button type="submit" disabled={saving}>
                    {saving ? 'Saving...' : 'Save'}
                </Button>
            </div>

        </form>
    )
}

export default TransactionForm;
import {useState} from "react";
import Select from "./ui/Select.jsx";
import Input from "../components/ui/Input.jsx";
import Button from "./ui/Button.jsx";
import api from "../assets/api.js";

const BudgetForm = ({ initial, categories, onSaved, onCancel }) => {
    const [form, setForm] = useState({
        categoryId: initial?.categoryId,
        amount: initial?.amount || '',
        period: initial?.period || 'MONTHLY',
        startDate: initial?.startDate
            ? initial.startDate.slice(0,10)
            : new Date().toISOString().slice(0,10),
    });
    const [saving, setSaving] = useState(false);

    const submit = async (e) => {
        e.preventDefault();
        setSaving(true);
        try {
            if (initial) {
                // UPDATE
                await api.put(`/api/budgets/${initial.id}`, {
                    amount: parseFloat(form.amount),
                    period: form.period,
                    startDate: `${form.startDate}T00:00:00`,
                    categoryId: Number(form.categoryId),
                });
            } else {
                await api.post(`/api/budgets`, {
                    amount: parseFloat(form.amount),
                    period: form.period,
                    startDate: `${form.startDate}T00:00:00`,
                    categoryId: Number(form.categoryId),
                });
            }
            onSaved();
        } catch (error) {
            console.error(error);
            console.error("STATUS:", error.response?.status);
            console.error("DATA:", error.response?.data);
            console.error("ERROR:", error);
        } finally {
            setSaving(false);
        }
    };

    return (
        <form onSubmit={submit} className="space-y-4">
            {!initial && (
                <Select
                    label="Category"
                    required
                    value={form.categoryId}
                    onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
                >
                    <option value="">Select a category</option>
                    {categories.map((category) => (
                        <option key={category.id} value={category.id}>
                            {category.categoryName}
                        </option>
                    ))}
                </Select>
            )}

            <Input
                label="Amount"
                type="number"
                step="0.01"
                min="0.01"
                required
                value={form.amount}
                onChange={(e) => setForm({ ...form, amount: e.target.value})}
            />

            <Select
                label="Period"
                value={form.period}
                onChange={(e) => setForm({ ...form, period: e.target.value})}
            >
                <option value="YEARLY">Yearly</option>
                <option value="MONTHLY">Monthly</option>
                <option value="WEEKLY">Weekly</option>
            </Select>

            <Input
                label="Start Date"
                type="date"
                required
                value={form.startDate}
                onChange={(e) => setForm({...form, startDate: e.target.value})}
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

export default BudgetForm;
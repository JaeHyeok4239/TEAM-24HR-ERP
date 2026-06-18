import api from "@/lib/api";

export const getPayrolls = async () => {

    const response = await api.get("/api/payrolls");

    return response.data;
};
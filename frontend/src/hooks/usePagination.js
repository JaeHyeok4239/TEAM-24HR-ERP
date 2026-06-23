import { useState, useEffect } from "react";

export const usePagination = (fetchFn, dependencies = []) => {
  const [page, setPage] = useState(0);
  const [data, setData] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [isFirst, setIsFirst] = useState(true);
  const [isLast, setIsLast] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const result = await fetchFn(page);
        setData(result.content);
        setTotalPages(result.totalPages);
        setIsFirst(result.first);
        setIsLast(result.last);
      } catch (err) {
        console.error(err.message);
      }
    };

    load();
  }, [page, ...dependencies]);

  return {
    data,
    page,
    setPage,
    totalPages,
    isFirst,
    isLast,
  };
};
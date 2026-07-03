// components/CommonPagination.js
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "../ui/pagination";
export const CommonPagination = ({ page, setPage, totalPages }) => {
  const pageNumbers = Array.from({ length: totalPages }, (_, i) => i);
  const isFirst = page === 0;
  const isLast = page === totalPages - 1;

  if (totalPages === 0) return null;

  return (
    <Pagination>
      <PaginationContent>
        <PaginationItem>
          <PaginationPrevious
            onClick={() => setPage(Math.max(0, page - 1))}
            className={isFirst ? "pointer-events-none opacity-50" : "cursor-pointer"}
          />
        </PaginationItem>

        {pageNumbers.map((num) => (
          <PaginationItem key={num}>
            <PaginationLink
              isActive={num === page}
              onClick={() => setPage(num)}
              className="cursor-pointer"
            >
              {num + 1}
            </PaginationLink>
          </PaginationItem>
        ))}

        <PaginationItem>
          <PaginationNext
            onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
            className={isLast ? "pointer-events-none opacity-50" : "cursor-pointer"}
          />
        </PaginationItem>
      </PaginationContent>
    </Pagination>
  );
};
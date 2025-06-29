import { colors } from '@/utils'
import styled from '@emotion/styled'

const PaginationWrapper = styled.div`
	display: flex;
	justify-content: center;
	gap: 8px;
	margin: 200px 0;

	button {
		padding: 8px 12px;
		border: 1px solid #ddd;
		background: white;
		color: #333;
		cursor: pointer;
		border-radius: 4px;
		transition: all 0.2s;

		&.active {
			background: ${colors.btnMainColor};
			color: white;
			border-color: ${colors.btnMainColor};
		}

		&:hover:not(.active) {
			background: #f1f1f1;
		}
	}
`

export const Pagination = ({
	currentPage,
	totalPages,
	onPageChange,
}: {
	currentPage: number
	totalPages: number
	onPageChange: (page: number) => void
}) => {
	const visiblePages = () => {
		const maxButtons = 10
		const pages = []

		let start = Math.max(1, currentPage - Math.floor(maxButtons / 2))
		let end = start + maxButtons - 1

		if (end > totalPages) {
			end = totalPages
			start = Math.max(1, end - maxButtons + 1)
		}

		for (let i = start; i <= end; i++) {
			pages.push(i)
		}

		return pages
	}

	return (
		<PaginationWrapper>
			{currentPage > 1 && (
				<button onClick={() => onPageChange(currentPage - 1)}>‹</button>
			)}

			{visiblePages().map(page => (
				<button
					key={page}
					className={page === currentPage ? 'active' : ''}
					onClick={() => onPageChange(page)}
				>
					{page}
				</button>
			))}

			{currentPage < totalPages && (
				<button onClick={() => onPageChange(currentPage + 1)}>›</button>
			)}
		</PaginationWrapper>
	)
}

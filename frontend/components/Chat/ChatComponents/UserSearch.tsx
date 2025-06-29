interface UserSearchProps {
	searchQuery: string
	setSearchQuery: (value: string) => void
}

export const UserSearch = ({
	searchQuery,
	setSearchQuery,
}: UserSearchProps) => {
	return (
		<div style={{ padding: '1rem' }}>
			<input
				type='text'
				placeholder='Поиск по имени или ID...'
				value={searchQuery}
				onChange={e => setSearchQuery(e.target.value)}
				style={{
					width: '100%',
					padding: '0.5rem',
					borderRadius: '6px',
					border: '1px solid #ccc',
					outline: 'none',
				}}
			/>
		</div>
	)
}

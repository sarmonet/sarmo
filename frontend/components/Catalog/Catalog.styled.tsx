import styled from '@emotion/styled'

export const CatalogWrapper = styled.section`
	display:flex;
	column-gap:40px;
	min-height: 1000px;
	@media(max-width: 1440px) {
		column-gap:20px;
	}
		@media (max-width: 1280px) {
			column-gap:40px;
			margin: 0 auto;
			padding: 0 40px;
	}
		@media (max-width: 768px) {
			column-gap:40px;
			padding: 0px;
	}
`

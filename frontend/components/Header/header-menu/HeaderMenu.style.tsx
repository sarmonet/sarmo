import { colors } from '@/utils'
import styled from '@emotion/styled'

export const Navigation = styled.nav`
	display: flex;
	align-items: center;

	// justify-content: space-between;
	// min-width: fit-content;
	li {
		position: relative;
		flex: 0 0 auto;
		&:after {
			content: '';
			position: absolute;
			bottom: 0;
			left: 50%;
			width: 0%;
			height: 1px;
			background-color: ${colors.btnMainColor};
			transition: 0.3s ease 0s;
		}
		&:hover:after {
			left: 0%;
			width: 100%;
		}
	}
`
export const List = styled.ul`
	display: flex;
	align-items: center;
	justify-content: space-between;

	font-weight: 500;
	font-size: 18px;
	line-height: 26px;
	column-gap: 25px;
	margin: 0 30px;

	.dropdown {
		position: absolute;
		width: 1133px;
		min-height: 362px;
		max-height: 400px;
		overflow-y: auto;
		top: 100%;
		left: -250%;
		background-color: #fcfcfc;
		padding: 60px 30px 30px 30px;
		border-radius: 0 0 16px 16px;
		line-height: 35px;
		display: flex;
		gap: 30px;
		border-left: 1px solid ${colors.borderColor};
		border-right: 1px solid ${colors.borderColor};
		border-bottom: 1px solid ${colors.borderColor};
		z-index: 1001;

		.categories {
			display: grid;
			grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));

			row-gap: 10px;
		}

.submenu {
  position: absolute;
  left: 300px;
  display: flex;
  padding-left: 65px;
  gap: 30px;
  cursor: pointer;
  color: ${colors.SecondGreyTextColor};
  border-left: 1px solid ${colors.borderColor};
  overflow-x: hidden; 
}

.submenu-column {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-right: 20px;
  border-right: 1px solid ${colors.borderColor};
  min-width: 200px;
}

	.submenu-column:last-child {
		border-right: none;
	}

	@media (max-width: 768px) {
		font-size: 21px;
		column-gap: 10px;
	}
`

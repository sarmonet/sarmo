import { colors } from '@/utils'
import styled from '@emotion/styled'

interface props {
	color?: string;
}

export const ListingItems = styled.ul`
	display: flex;
	flex-direction: column;
	row-gap: 20px;
`;

export const ListingItem = styled.li`
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 24px;	
	border-radius: 20px;
	border: 1px solid ${colors.borderColor};

`;
export const ListingMain = styled.div`
	display: flex;
	column-gap: 20px;
	img{
		height: 200px;
	}
	
`
export const ListingInfo = styled.div`
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	h2{
		font-size: 32px;
		line-height: 42px;
	};
	h3{
		font-size: 28px;
		line-height: 38px;
		color: ${colors.btnMainColor};
	}
	span{
		font-size: 18px;
		line-height: 26px;
		font-weight: 400;
		margin-right: 130px;
		color: ${colors.greyTextColor};
	}
`
export const ListingSpecial = styled.p<props>`
	
		border-radius: 180px;
		border: 1px solid ${colors.borderColor};
		color: ${({ color }) => color ? color : colors.mainWhiteTextColor};
		padding: 5px 10px;
		font-size: 18px;
		line-height: 26px;
		font-weight: 400;
	
`
export const ListingButtons = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 20px;
	
`;

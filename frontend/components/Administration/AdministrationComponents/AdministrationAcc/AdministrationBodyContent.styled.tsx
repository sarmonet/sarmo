import { colors } from '@/utils'
import styled from '@emotion/styled'
export const AdminAccWrapper = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 40px;
`
export const AdminAccBlocks = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 30px;

`
export const AdminAccBlock = styled.div`
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 10px 0;
	button{
		padding: 10px;
		border-radius: 50%;
		border-color: #F7F9FB;
	}
`
export const AdminAccContent = styled.div`
	width: 410px;
	font-size: 28px;
	line-height: 38px;
	font-weight: 500;
	span{
		color: ${colors.subtitleTextColor};
	}
`
export const AdminAccTop = styled.div`
	display: flex;
	gap: 10px;
	align-items: center;
`
export const AdminAccTitle = styled.h2`
	font-size: 32px;
	line-height: 43px;
	font-weight: 500;
	line-height: 42px;
	width: 410px;
	color: ${colors.btnMainColor};
`

export const AdminRoleChange = styled.div`
	position: absolute;
	right: 0%;
	width: fit-content;
	height: 125px;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);

	border-radius: 18px;
	z-index: 4;
	font-size: 16px;
	background: #fff;
	button{
		display: flex;
		align-items: center;
		gap: 10px;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		font-size: 15px;
		transition: all 0.3s ease;
		&:hover{
			color:  ${colors.btnMainColor};
		}
	}

`



'use client'

import { FC, ReactNode } from 'react'
import { BigTitle } from './Title.style'

export const Title:FC<{children: ReactNode;isWhite?: boolean}> = ({children , isWhite = false}) => {
	return (
		<BigTitle isWhite={isWhite}>{children}</BigTitle>
	)
}

import { useEffect, useRef, useState } from 'react'

type CodeInputProps = {
	length?: number
	onSubmit: (code: string) => void
}

export const CodeInput = ({ length = 6, onSubmit }: CodeInputProps) => {
	const [values, setValues] = useState<string[]>(Array(length).fill(''))
	const inputsRef = useRef<(HTMLInputElement | null)[]>([])

	useEffect(() => {
		inputsRef.current[0]?.focus()
	}, [])

	const handleChange = (
		index: number,
		e: React.ChangeEvent<HTMLInputElement>
	) => {
		const inputValue = e.target.value
		const filtered = inputValue.replace(/\D/g, '')

		if (!filtered) return

		const updatedValues = [...values]
		updatedValues[index] = filtered[0]
		setValues(updatedValues)

		if (index < length - 1) {
			inputsRef.current[index + 1]?.focus()
		}

		const fullCode = updatedValues.join('')
		if (fullCode.length === length && !updatedValues.includes('')) {
			onSubmit(fullCode)
		}
	}

	const handleKeyDown = (
		index: number,
		e: React.KeyboardEvent<HTMLInputElement>
	) => {
		if (e.key === 'Backspace') {
			const updatedValues = [...values]

			if (values[index]) {
				updatedValues[index] = ''
				setValues(updatedValues)
			} else if (index > 0) {
				inputsRef.current[index - 1]?.focus()
				updatedValues[index - 1] = ''
				setValues(updatedValues)
			}
		}
	}

	const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
		e.preventDefault()
		const pasted = e.clipboardData
			.getData('Text')
			.replace(/\D/g, '')
			.slice(0, length)
		if (pasted.length === 0) return

		const newValues = [...values]
		for (let i = 0; i < length; i++) {
			newValues[i] = pasted[i] || ''
		}

		setValues(newValues)

		const lastIndex = Math.min(pasted.length, length - 1)
		inputsRef.current[lastIndex]?.focus()

		if (pasted.length === length) {
			onSubmit(pasted)
		}
	}

	return (
		<div style={{ display: 'flex', gap: '8px', justifyContent: 'center' }}>
			{values.map((val, i) => (
				<input
					key={i}
					type='text'
					inputMode='numeric'
					autoComplete='one-time-code'
					maxLength={1}
					value={val}
					onChange={e => handleChange(i, e)}
					onKeyDown={e => handleKeyDown(i, e)}
					onPaste={handlePaste}
					ref={el => {
						inputsRef.current[i] = el
					}}
					style={{
						width: '40px',
						height: '48px',
						fontSize: '24px',
						textAlign: 'center',
						border: '1px solid #ccc',
						borderRadius: '6px',
					}}
				/>
			))}
		</div>
	)
}

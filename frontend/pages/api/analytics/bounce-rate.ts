// pages/api/analytics/bounce-rate.ts

import { BetaAnalyticsDataClient } from '@google-analytics/data'
import fs from 'fs'
import type { NextApiRequest, NextApiResponse } from 'next'
import os from 'os'
import path from 'path'

const propertyId = process.env.GA4_PROPERTY_ID
const credentialsJson = process.env.GOOGLE_APPLICATION_CREDENTIALS_JSON

const handler = async (req: NextApiRequest, res: NextApiResponse) => {
	console.log('[API/bounce-rate] Request received.')

	if (!propertyId || !credentialsJson) {
		console.error('[API/bounce-rate] ERROR: GA4 configuration missing.')
		console.error(`  GA4_PROPERTY_ID: ${propertyId ? 'Set' : 'Not Set'}`)
		console.error(
			`  GOOGLE_APPLICATION_CREDENTIALS_JSON: ${
				credentialsJson
					? 'Set (length: ' + credentialsJson.length + ')'
					: 'Not Set'
			}`
		)
		return res.status(500).json({
			error: 'GA4 конфигурация отсутствует. Проверьте переменные окружения.',
		})
	}

	if (typeof propertyId !== 'string' || !/^\d+$/.test(propertyId)) {
		console.error(
			'[API/bounce-rate] ERROR: GA4_PROPERTY_ID is invalid. It should be a numeric string (e.g., "123456789"), not starting with "G-".'
		)
		return res
			.status(500)
			.json({ error: 'Неверный формат GA4_PROPERTY_ID. Должен быть числовым.' })
	}

	let tempKeyPath: string | undefined

	try {
		tempKeyPath = path.join(os.tmpdir(), `ga4-key-${Date.now()}.json`)
		try {
			JSON.parse(credentialsJson)
		} catch (parseError) {
			console.error(
				'[API/bounce-rate] ERROR: GOOGLE_APPLICATION_CREDENTIALS_JSON is not a valid JSON string:',
				parseError
			)
			return res.status(500).json({
				error:
					'GOOGLE_APPLICATION_CREDENTIALS_JSON не является корректной JSON строкой.',
			})
		}

		fs.writeFileSync(tempKeyPath, credentialsJson)
		process.env.GOOGLE_APPLICATION_CREDENTIALS = tempKeyPath
		console.log(
			'[API/bounce-rate] Temporary key file created successfully at:',
			tempKeyPath
		)

		const client = new BetaAnalyticsDataClient()
		console.log('[API/bounce-rate] BetaAnalyticsDataClient initialized.')

		const startDate = (req.query.startDate as string) || '30daysAgo'
		const endDate = (req.query.endDate as string) || 'today'

		const requestBody = {
			property: `properties/${propertyId}`,
			dateRanges: [{ startDate, endDate }],
			metrics: [{ name: 'bounces' }, { name: 'sessions' }],
			dimensions: [{ name: 'date' }],
			orderBys: [{ dimension: { dimensionName: 'date' } }],
		}

		console.log(
			'[API/bounce-rate] Sending GA4 Data API runReport request with:'
		)
		console.log(JSON.stringify(requestBody, null, 2))

		const [response] = await client.runReport(requestBody)

		console.log('[API/bounce-rate] GA4 Data API response received.')

		const dailyBounceRateData: { date: string; bounceRate: string }[] = []

		if (response.rows && response.rows.length > 0) {
			for (const row of response.rows) {
				const date = row.dimensionValues?.[0]?.value || 'N/A'
				const bounces = parseInt(row.metricValues?.[0]?.value || '0', 10)
				const sessions = parseInt(row.metricValues?.[1]?.value || '0', 10)

				let bounceRate = 0
				if (sessions > 0) {
					bounceRate = (bounces / sessions) * 100
				}
				dailyBounceRateData.push({ date, bounceRate: bounceRate.toFixed(2) })
			}
		} else {
			console.log(
				'[API/bounce-rate] No rows returned from GA4 for bounce rate.'
			)
		}

		console.log(
			`[API/bounce-rate] Processed ${dailyBounceRateData.length} daily entries for bounce rate.`
		)
		res.status(200).json(dailyBounceRateData)
	} catch (error: unknown) {
		console.error('[API/bounce-rate] ERROR caught during GA4 API call:')
		if (error instanceof Error) {
			console.error('  Error name:', error.name)
			console.error('  Error message:', error.message)
			if (error.stack) {
				console.error('  Error stack:', error.stack)
			}
		}
		if (typeof error === 'object' && error !== null) {
			const gapiError = error as {
				code?: number
				details?: string
				metadata?: object
				message?: string
			}
			if (gapiError.code) {
				console.error('  Google API error code:', gapiError.code)
			}
			if (gapiError.details) {
				console.error('  Google API details:', gapiError.details)
			}
			if (gapiError.metadata) {
				console.error('  Google API metadata:', gapiError.metadata)
			}
		}

		res.status(500).json({
			error: 'Не удалось получить показатель отказов',
			details: String(error),
		})
	} finally {
		if (tempKeyPath && fs.existsSync(tempKeyPath)) {
			console.log(
				`[API/bounce-rate] Deleting temporary key file: ${tempKeyPath}`
			)
			fs.unlinkSync(tempKeyPath)
			delete process.env.GOOGLE_APPLICATION_CREDENTIALS
			console.log('[API/bounce-rate] Temporary key file deleted.')
		} else {
			console.log(
				'[API/bounce-rate] No temporary key file to delete or file not found.'
			)
		}
		console.log('[API/bounce-rate] Request processing finished.')
	}
}

export default handler

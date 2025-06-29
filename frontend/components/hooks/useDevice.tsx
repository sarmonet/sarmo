import { useEffect, useState } from 'react'

export const useDevice = () => {
    const [device, setDevice] = useState({
        isDesktop: false,
        isLargeTablet: false,
        isTablet: false,
        isMobile: false
    })

    useEffect(() => {
        const updateDevice = () => {
            const width = window.innerWidth

            if (width >= 1025) {
                setDevice({
                    isDesktop: true,
                    isTablet: false,
                    isLargeTablet: false,
                    isMobile: false,
                })
            } else if (width >= 880) {
                setDevice({
                    isDesktop: false,
                    isTablet: false,
                    isLargeTablet: true,
                    isMobile: false
                })
            } else if (width >= 490) {
                setDevice({
                    isDesktop: false,
                    isTablet: true,
                    isLargeTablet: false,
                    isMobile: false
                })
            } else {
                setDevice({
                    isDesktop: false,
                    isTablet: false,
                    isLargeTablet: false,
                    isMobile: true
                })
            }
        }

        updateDevice()

        window.addEventListener('resize', updateDevice)

        return () => {
            window.removeEventListener('resize', updateDevice)
        }
    }, [])

    return device
}

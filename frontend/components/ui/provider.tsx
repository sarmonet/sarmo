"use client"

import { ChakraProvider, defaultSystem } from "@chakra-ui/react"
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function Provider(props: any) { 
  return (
    <ChakraProvider value={defaultSystem}>
      {props.children}
    </ChakraProvider>
  )
}
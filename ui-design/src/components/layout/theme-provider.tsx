import {
  ThemeProvider as NextThemesProvider,
  type ThemeProviderProps,
  useTheme,
} from "next-themes";

/**
 * Wrapper de next-themes para toda la app. Sincroniza el tema con la clase
 * `dark` en <html> (así lo espera el layer `@custom-variant dark` de
 * index.css) y respeta la preferencia del sistema operativo por defecto.
 */
function ThemeProvider({
  children,
  attribute = "class",
  defaultTheme = "system",
  enableSystem = true,
  disableTransitionOnChange = true,
  ...props
}: ThemeProviderProps) {
  return (
    <NextThemesProvider
      attribute={attribute}
      defaultTheme={defaultTheme}
      enableSystem={enableSystem}
      disableTransitionOnChange={disableTransitionOnChange}
      {...props}
    >
      {children}
    </NextThemesProvider>
  );
}

export { ThemeProvider, useTheme };

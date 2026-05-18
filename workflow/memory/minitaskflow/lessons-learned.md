# MiniTaskFlow Lessons Learned

No lessons recorded yet.

## 2026-05-18T12:17:43Z - Maven Test Sandbox Attach Failure

Mockito/Byte Buddy may fail to self-attach to the JVM when `mvn test` runs inside the sandbox. If the failure signature is Byte Buddy attach initialization and no test assertion or compile error is present, rerun the exact Maven validation with permitted JVM attach behavior before changing backend code.

## 2026-05-18T12:26:31Z - Vite TypeScript 6 Build Compatibility

For Vite projects using current TypeScript, use `moduleResolution: "Bundler"` and include `src/vite-env.d.ts` with `/// <reference types="vite/client" />` so `import.meta.env` and CSS side-effect imports type-check cleanly.

## 2026-05-18T12:40:15Z - Playwright Local Runtime Paths and Sandbox

When Playwright runs from a nested frontend workspace, reporter paths must walk back to the factory root before writing `workflow/playwright/*`. Browser launches on macOS may also need to run outside the sandbox because Chromium Mach port registration can be denied. Avoid broad text selectors when labels and statuses share the same text.

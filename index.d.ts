declare module 'react-native-floating-bubble' {
	export type title = {
		title: string;
		color?: string;
	};
	export type buttonItem = {
		title: string;
		value: string;
		color?: string;
	};

	export function showFloatingBubble(
		titleData: title,
		data: buttonItem[]
	): void;
	export function hideFloatingBubble(): void;
	export function checkPermission(): void;
	export function requestPermission(): void;
	export function initialize(): void;
}
